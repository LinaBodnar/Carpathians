using System.Collections.Generic;

namespace Carpathians.DAL.Entities
{
    public class RoutePoint
    {
        public int Id { get; set; }
        public int RouteId { get; set; }
        public Route? Route { get; set; }

        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public int OrderIndex { get; set; } // Визначає порядок відображення в акордеоні

        // Зв'язок: фотографії цієї конкретної точки
        public ICollection<RoutePointPhoto> Photos { get; set; } = new List<RoutePointPhoto>();
    }
}