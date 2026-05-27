using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Carpathians.BLL.Interfaces;

namespace Carpathians.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class RoutesController : ControllerBase
    {
        private readonly IRouteService _routeService;

        public RoutesController(IRouteService routeService)
        {
            _routeService = routeService;
        }

        // GET: api/routes
        [HttpGet]
        public async Task<IActionResult> GetRoutes()
        {
            var routes = await _routeService.GetRoutesAsync();
            return Ok(routes);
        }

        // GET: api/routes/5
        [HttpGet("{id:int}")]
        public async Task<IActionResult> GetRouteById(int id)
        {
            var route = await _routeService.GetRouteByIdAsync(id);
            if (route == null) return NotFound(new { message = "Маршрут не знайдено" });
            return Ok(route);
        }

        // GET: api/routes/slug/hoverla
        [HttpGet("slug/{slug}")]
        public async Task<IActionResult> GetRouteBySlug(string slug)
        {
            var route = await _routeService.GetRouteBySlugAsync(slug);
            if (route == null) return NotFound(new { message = "Маршрут не знайдено" });
            return Ok(route);
        }
    }
}