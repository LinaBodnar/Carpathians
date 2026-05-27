using System.Collections.Generic;
using System.Threading.Tasks;
using AutoMapper;
using Carpathians.BLL.DTOs;
using Carpathians.BLL.Interfaces;
using Carpathians.DAL.Entities;
using Carpathians.DAL.Interfaces;

namespace Carpathians.BLL.Services
{
    public class GalleryService : IGalleryService
    {
        private readonly IGenericRepository<GalleryPhoto> _galleryRepository;
        private readonly IMapper _mapper;

        public GalleryService(IGenericRepository<GalleryPhoto> galleryRepository, IMapper mapper)
        {
            _galleryRepository = galleryRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<GalleryPhotoDto>> GetGalleryPhotosAsync(int? routeId = null)
        {
            IEnumerable<GalleryPhoto> photos;

            if (routeId.HasValue)
            {
                // Якщо обрано конкретний фільтр гори (наприклад, Гоverla)
                photos = await _galleryRepository.GetAllAsync(p => p.RouteId == routeId.Value);
            }
            else
            {
                // Вкладка "Всі фото"
                photos = await _galleryRepository.GetAllAsync();
            }

            return _mapper.Map<IEnumerable<GalleryPhotoDto>>(photos);
        }
    }
}