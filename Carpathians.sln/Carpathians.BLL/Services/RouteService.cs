using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using AutoMapper;
using Carpathians.BLL.DTOs;
using Carpathians.BLL.Interfaces;
using Carpathians.DAL.Entities;
using Carpathians.DAL.Interfaces;

namespace Carpathians.BLL.Services
{
    public class RouteService : IRouteService
    {
        private readonly IGenericRepository<Route> _routeRepository;
        private readonly IMapper _mapper;

        public RouteService(IGenericRepository<Route> routeRepository, IMapper mapper)
        {
            _routeRepository = routeRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<RouteDto>> GetRoutesAsync()
        {
            // Завантажуємо маршрути разом із точками та фотографіями точок
            var routes = await _routeRepository.GetAllAsync(includeProperties: "RoutePoints.Photos");

            var dtos = _mapper.Map<IEnumerable<RouteDto>>(routes).ToList();

            // Сортуємо точки в кожному маршруті за індексом для акордеона
            foreach (var route in dtos)
            {
                route.RoutePoints = route.RoutePoints.OrderBy(p => p.OrderIndex).ToList();
            }

            return dtos;
        }

        public async Task<RouteDto?> GetRouteByIdAsync(int id)
        {
            var route = await _routeRepository.GetFirstOrDefaultAsync(r => r.Id == id, includeProperties: "RoutePoints.Photos");
            if (route == null) return null;

            var dto = _mapper.Map<RouteDto>(route);
            dto.RoutePoints = dto.RoutePoints.OrderBy(p => p.OrderIndex).ToList();
            return dto;
        }

        public async Task<RouteDto?> GetRouteBySlugAsync(string slug)
        {
            var route = await _routeRepository.GetFirstOrDefaultAsync(r => r.Slug.ToLower() == slug.ToLower(), includeProperties: "RoutePoints.Photos");
            if (route == null) return null;

            var dto = _mapper.Map<RouteDto>(route);
            dto.RoutePoints = dto.RoutePoints.OrderBy(p => p.OrderIndex).ToList();
            return dto;
        }
    }
}