using System.Collections.Generic;
using System.Threading.Tasks;
using Carpathians.MAUI.Models;
using Carpathians.DAL.Entities;

namespace Carpathians.MAUI.Services;

public interface IDataService
{
    Task<IEnumerable<RouteModel>> GetAllRoutesAsync();
    Task<RouteModel> GetRouteByKeyAsync(string key);
    Task<IEnumerable<GalleryPhotoModel>> GetAllPhotosAsync();
    Task<IEnumerable<BookingModel>> GetUserBookingsAsync(int userId);
    Task<BookingModel> CreateBookingAsync(BookingModel booking);
    Task<User> LoginAsync(string email, string password);
    Task<User> RegisterAsync(string name, string email, string password);
    Task UpdateUserAsync(User user);
    Task EnsureSeedDataAsync();
}