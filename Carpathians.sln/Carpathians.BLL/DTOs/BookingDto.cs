using System;

namespace Carpathians.BLL.DTOs
{
    public class BookingDto
    {
        public int Id { get; set; }
        public int RouteId { get; set; }
        public string RouteName { get; set; } = string.Empty;
        public int? UserId { get; set; }
        public string GuestName { get; set; } = string.Empty;
        public string GuestEmail { get; set; } = string.Empty;
        public string GuestPhone { get; set; } = string.Empty;
        public DateTime TourDate { get; set; }
        public int NumberOfPeople { get; set; }
        public string Status { get; set; } = "pending";
        public DateTime CreatedAt { get; set; }
    }
}