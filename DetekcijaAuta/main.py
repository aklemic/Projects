import cv2
import numpy as np
from ultralytics import YOLO

model = YOLO("yolov8n.pt")

cap = cv2.VideoCapture("C:/Users/Antonio/Desktop/DetetkcijaAuta/DayDrive1.mp4")

if not cap.isOpened():
    print("Greška: video nije učitan.")
    exit()

total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
paused = False
output = None

while True:
    if not paused:
        ret, frame = cap.read()
        if not ret:
            break
        
        h, w = frame.shape[:2]
        
        # 1. Definiranje naše vozne trake (Poligon/Trapez)
        pt_top_left = (int(w * 0.45), int(h * 0.65))
        pt_top_right = (int(w * 0.55), int(h * 0.65))
        pt_bottom_right = (int(w * 0.75), h)
        pt_bottom_left = (int(w * 0.25), h)

        lane_polygon = np.array([[pt_top_left, pt_top_right, pt_bottom_right, pt_bottom_left]], np.int32)

        # YOLO detekcija s praćenjem (tracking)
        results = model.track(frame, classes=[2, 5, 7], persist=True, tracker="bytetrack.yaml", verbose=False, imgsz=640)
        
        output = frame.copy()
        
        overlay = output.copy()
        
        cv2.fillPoly(overlay, lane_polygon, (255, 0, 0))
        cv2.addWeighted(overlay, 0.2, output, 0.8, 0, output)
        cv2.polylines(output, lane_polygon, isClosed=True, color=(255, 50, 50), thickness=2)

        if not results[0].boxes:
            cv2.imshow("ADAS Detekcija Vozila", output)
            key = cv2.waitKey(1) & 0xFF
            continue
            
        result = results[0]
        vehicle_count = len(result.boxes)
        
        best_front_box = None
        best_front_id = None
        max_bottom_y = 0 

        # 2. Prolaz: Pronađi glavno vozilo SAMO unutar naše trake
        for box in result.boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            center_x = (x1 + x2) // 2
            
            bottom_center = (center_x, y2)
            
            is_in_lane = cv2.pointPolygonTest(lane_polygon, bottom_center, False) >= 0
            
            if is_in_lane:
                if y2 > max_bottom_y:
                    max_bottom_y = y2
                    best_front_box = (x1, y1, x2, y2)
                    best_front_id = int(box.id[0]) if box.id is not None else "?"

        poly_color = (255, 0, 0)
        warning_active = False

        # 3. Prolaz: Crtanje svih vozila i Proximity logika za glavno vozilo
        for box in result.boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            conf = float(box.conf[0])
            track_id = int(box.id[0]) if box.id is not None else "?"
            
            box_width = x2 - x1
            box_height = y2 - y1

            if box_width < 40 or box_height < 40 or conf < 0.4:
                continue

            is_main = (best_front_box is not None) and ((x1, y1, x2, y2) == best_front_box)

            color = (0, 255, 0)
            label = f"car ID:{track_id} [{conf:.2f}]"
            thickness = 2

            # Proximity logika (Procjena udaljenosti temeljem širine)
            if is_main:
                thickness = 3
                width_ratio = box_width / w
                
                if width_ratio > 0.35: 
                    color = (0, 0, 255) 
                    label = f"UPOZORENJE! ID:{track_id}"
                    poly_color = (0, 0, 255) 
                    warning_active = True
                elif width_ratio > 0.20: 
                    color = (0, 165, 255) 
                    label = f"prati ID:{track_id}"
                    poly_color = (0, 165, 255) 
                else: 
                    color = (0, 255, 255)
                    label = f"u_traci ID:{track_id}"
                    poly_color = (0, 200, 200) 

            cv2.rectangle(output, (x1, y1), (x2, y2), color, thickness)
            text_y = y1 - 10 if y1 > 25 else y1 + 25
            cv2.putText(output, label, (x1, text_y), cv2.FONT_HERSHEY_SIMPLEX, 0.6, color, 2)

        # 4. Osvježavanje i iscrtavanje dinamičkog poligona (mijenja boju s opasnošću)
        overlay = output.copy()
        cv2.fillPoly(overlay, lane_polygon, poly_color)
        cv2.addWeighted(overlay, 0.3, output, 0.7, 0, output)
        cv2.polylines(output, lane_polygon, isClosed=True, color=poly_color, thickness=2)

        # 5. Vizualni HUD alarm (Crveni bljesak na rubovima ekrana)
        if warning_active:
            cv2.rectangle(output, (0, 0), (w, h), (0, 0, 255), 15) 
            cv2.putText(output, "KOCI!", (w // 2 - 50, h // 2), cv2.FONT_HERSHEY_DUPLEX, 2, (0, 0, 255), 4)

        # 6. Prikaz UI informacija (Brojač vozila)
        cv2.putText(output, f"Ukupno vozila: {vehicle_count}", (20, 40), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)
        if best_front_box is None:
            cv2.putText(output, "Nema vozila u traci", (20, 75), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
        else:
            cv2.putText(output, "Vozilo detektirano u traci!", (20, 75), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 165, 255), 2)

    else:
        if output is not None:
            cv2.imshow("ADAS Detekcija Vozila", output)

    cv2.imshow("ADAS Detekcija Vozila", output)
    
    # 7. Kontrole s tipkovnice
    key = cv2.waitKey(1 if not paused else 30) & 0xFF

    if key == 27: 
        break
    elif key == 32: 
        paused = not paused
    elif key == ord('d'): 
        for _ in range(30):
            cap.read()

cap.release()
cv2.destroyAllWindows()