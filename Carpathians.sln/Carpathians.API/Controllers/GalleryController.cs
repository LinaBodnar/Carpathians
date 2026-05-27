using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Carpathians.BLL.Interfaces;

namespace Carpathians.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class GalleryController : ControllerBase
    {
        private readonly IGalleryService _galleryService;

        public GalleryController(IGalleryService galleryService)
        {
            _galleryService = galleryService;
        }

        // GET: api/gallery або api/gallery?routeId=3
        [HttpGet]
        public async Task<IActionResult> GetPhotos([FromQuery] int? routeId = null)
        {
            var photos = await _galleryService.GetGalleryPhotosAsync(routeId);
            return Ok(photos);
        }
    }
}