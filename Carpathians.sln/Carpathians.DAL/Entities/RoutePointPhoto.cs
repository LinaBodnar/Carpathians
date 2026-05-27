namespace Carpathians.DAL.Entities
{
    public class RoutePointPhoto
    {
        public int Id { get; set; }
        public int RoutePointId { get; set; }
        public RoutePoint? RoutePoint { get; set; }

        public string Url { get; set; } = string.Empty;
    }
}