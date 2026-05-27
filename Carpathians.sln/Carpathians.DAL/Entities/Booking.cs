using System;

namespace Carpathians.DAL.Entities
{
    public class Booking
    {
        public int Id { get; set; }

        public int RouteId { get; set; }
        public Route? Route { get; set; }

        // Зв'язок з користувачем: може бути null, якщо бронює незалогінений гість
        public int? UserId { get; set; }
        public User? User { get; set; }

        // Окремо зберігаємо дані покупця на момент заявки (завжди заповнені)
        public string GuestName { get; set; } = string.Empty;
        public string GuestEmail { get; set; } = string.Empty;
        public string GuestPhone { get; set; } = string.Empty;

        public DateTime TourDate { get; set; }
        public int NumberOfPeople { get; set; }
        public string Status { get; set; } = "pending"; // pending / confirmed
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    }
}