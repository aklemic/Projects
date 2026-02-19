using System;

namespace SportResultsApp.Models
{
    public class InvalidMatchDataException : Exception
    {
        public InvalidMatchDataException() { }

        public InvalidMatchDataException(string message) : base(message) { }

        public InvalidMatchDataException(string message, Exception inner) : base(message, inner) { }
    }
}
