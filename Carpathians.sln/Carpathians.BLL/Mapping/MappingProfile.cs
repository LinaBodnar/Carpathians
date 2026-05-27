using AutoMapper;
using Carpathians.DAL.Entities;
using Carpathians.BLL.DTOs;

namespace Carpathians.BLL.Mapping // <--- ЦЕЙ РЯДОК МАЄ БУТИ САМЕ ТАКИМ
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            CreateMap<User, UserDto>();

            CreateMap<Route, RouteDto>()
                .ForMember(dest => dest.RoutePoints, opt => opt.MapFrom(src => src.RoutePoints));

            CreateMap<RoutePoint, RoutePointDto>()
                .ForMember(dest => dest.Photos, opt => opt.MapFrom(src => src.Photos));

            CreateMap<RoutePointPhoto, RoutePointPhotoDto>();

            CreateMap<GalleryPhoto, GalleryPhotoDto>();

            CreateMap<Booking, BookingDto>()
                .ForMember(dest => dest.RouteName, opt => opt.MapFrom(src => src.Route != null ? src.Route.Name : string.Empty));

            CreateMap<BookingDto, Booking>();
        }
    }
}