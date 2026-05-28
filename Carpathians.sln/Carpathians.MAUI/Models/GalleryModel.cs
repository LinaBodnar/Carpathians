namespace Carpathians.MAUI.Models
{
    public class GalleryModel
    {
        public int Id { get; set; }
        public string ImageUrl { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public int RouteId { get; set; }
    }
}