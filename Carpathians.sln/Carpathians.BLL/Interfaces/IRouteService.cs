using System.Collections.Generic;
using System.Threading.Tasks;
using Carpathians.BLL.DTOs;

namespace Carpathians.BLL.Interfaces
{
    public interface IRouteService
    {
        Task<IEnumerable<RouteDto>> GetRoutesAsync();
        Task<RouteDto?> GetRouteByIdAsync(int id);
        Task<RouteDto?> GetRouteBySlugAsync(string slug);
    }
}