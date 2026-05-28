using System.Collections.Generic;

namespace Carpathians.DAL.Entities
{
    public class Route
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public string Difficulty { get; set; } = string.Empty;
        public double DistanceKm { get; set; }
        public int DurationHours { get; set; }
        public string ImageUrl { get; set; } = string.Empty;
        public string MapUrl { get; set; } = string.Empty;
        public string Slug { get; set; } = string.Empty;
        public string WhatToTake { get; set; } = string.Empty;

        public ICollection<RoutePoint> RoutePoints { get; set; } = new List<RoutePoint>();
        public ICollection<GalleryPhoto> GalleryPhotos { get; set; } = new List<GalleryPhoto>();
        public ICollection<Booking> Bookings { get; set; } = new List<Booking>();
    }
}