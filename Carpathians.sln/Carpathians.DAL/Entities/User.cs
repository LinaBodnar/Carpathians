using System;
using System.Collections.Generic;

namespace Carpathians.DAL.Entities
{
    public class User
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string PasswordHash { get; set; } = string.Empty; // Хешований пароль (не plain text)
        public string? AvatarUrl { get; set; } // Посилання на фото (null = показуємо ініціали)
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        // Зв'язок: один користувач може мати багато заявок
        public ICollection<Booking> Bookings { get; set; } = new List<Booking>();
    }
}