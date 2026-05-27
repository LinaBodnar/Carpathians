namespace Carpathians.BLL.DTOs
{
    public class GalleryPhotoDto
    {
        public int Id { get; set; }
        public int? RouteId { get; set; }
        public string Url { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
    }
}