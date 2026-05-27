using System.Collections.Generic;

namespace Carpathians.DAL.Entities
{
    public class Route
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string Difficulty { get; set; } = string.Empty; // easy/medium/hard або зелений/жовтий/червоний
        public double DistanceKm { get; set; }
        public int DurationHours { get; set; }
        public string ImageUrl { get; set; } = string.Empty;
        public string MapUrl { get; set; } = string.Empty; // Посилання на Mapy.cz для WebView
        public string Slug { get; set; } = string.Empty; // Красиві URL типу /routes/hoverla

        // Зберігаємо список "Що взяти з собою" як текст (наприклад, через кому або з абзацами)
        public string WhatToTake { get; set; } = string.Empty;

        // Навігаційні властивості (зв'язки)
        public ICollection<RoutePoint> RoutePoints { get; set; } = new List<RoutePoint>();
        public ICollection<GalleryPhoto> GalleryPhotos { get; set; } = new List<GalleryPhoto>();
        public ICollection<Booking> Bookings { get; set; } = new List<Booking>();
    }
}