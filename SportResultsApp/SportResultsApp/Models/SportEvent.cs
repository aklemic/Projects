namespace SportResultsApp.Models
{
    public abstract class SportEvent
    {
        public System.DateTime Date { get; set; }

        public SportEvent(System.DateTime date)
        {
            Date = date;
        }

        public abstract string GetEventInfo();
    }

    public interface IExportable
    {
        string ToCsv();
    }
}
