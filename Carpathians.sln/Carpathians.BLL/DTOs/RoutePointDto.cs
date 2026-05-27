using System.Collections.Generic;

namespace Carpathians.BLL.DTOs
{
    public class RoutePointDto
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public int OrderIndex { get; set; }
        public List<RoutePointPhotoDto> Photos { get; set; } = new List<RoutePointPhotoDto>();
    }

    public class RoutePointPhotoDto
    {
        public int Id { get; set; }
        public string Url { get; set; } = string.Empty;
    }
}