using System;

namespace Carpathians.DAL.Entities
{
    public class Booking
    {
        public int Id { get; set; }

        public int RouteId { get; set; }
        public Route? Route { get; set; }

        public int? UserId { get; set; }
        public User? User { get; set; }

        public string GuestName { get; set; } = string.Empty;
        public string GuestEmail { get; set; } = string.Empty;
        public string GuestPhone { get; set; } = string.Empty;

        public DateTime TourDate { get; set; }
        public int NumberOfPeople { get; set; }
        public string Status { get; set; } = "pending";
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}