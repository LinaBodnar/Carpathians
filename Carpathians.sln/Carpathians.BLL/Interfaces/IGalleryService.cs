using System.Collections.Generic;
using System.Threading.Tasks;
using Carpathians.BLL.DTOs;

namespace Carpathians.BLL.Interfaces
{
    public interface IGalleryService
    {
        Task<IEnumerable<GalleryPhotoDto>> GetGalleryPhotosAsync(int? routeId = null);
    }
}