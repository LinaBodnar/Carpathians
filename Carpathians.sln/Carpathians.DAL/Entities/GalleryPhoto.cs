namespace Carpathians.DAL.Entities
{
    public class GalleryPhoto
    {
        public int Id { get; set; }

        // Якщо route_id = null, фото потрапляє у "Всі фото". Якщо вказано — працює фільтр горы.
        public int? RouteId { get; set; }
        public Route? Route { get; set; }

        public string Url { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
    }
}