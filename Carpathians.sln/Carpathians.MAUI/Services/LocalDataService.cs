using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using Carpathians.DAL.Entities;
using Carpathians.DAL.Interfaces;
using Carpathians.MAUI.Models;

namespace Carpathians.MAUI.Services;

public class LocalDataService : IDataService
{
    private readonly IGenericRepository<Route> _routeRepo;
    private readonly IGenericRepository<Booking> _bookingRepo;
    private readonly IGenericRepository<User> _userRepo;
    private readonly IGenericRepository<GalleryPhoto> _galleryRepo;

    public LocalDataService(
        IGenericRepository<Route> routeRepo,
        IGenericRepository<Booking> bookingRepo,
        IGenericRepository<User> userRepo,
        IGenericRepository<GalleryPhoto> galleryRepo)
    {
        _routeRepo = routeRepo;
        _bookingRepo = bookingRepo;
        _userRepo = userRepo;
        _galleryRepo = galleryRepo;
    }

    private string HashPassword(string password)
    {
        using var sha256 = SHA256.Create();
        var hashedBytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
        return BitConverter.ToString(hashedBytes).Replace("-", "").ToLower();
    }

    public async Task<User> LoginAsync(string email, string password)
    {
        var hash = HashPassword(password);
        return await _userRepo.GetFirstOrDefaultAsync(u => u.Email == email && u.PasswordHash == hash);
    }

    public async Task<User> RegisterAsync(string name, string email, string password)
    {
        var existing = await _userRepo.GetFirstOrDefaultAsync(u => u.Email == email);
        if (existing != null) throw new Exception("Користувач з таким email вже існує");

        var user = new User
        {
            Name = name,
            Email = email,
            PasswordHash = HashPassword(password),
            CreatedAt = DateTime.UtcNow
        };
        await _userRepo.AddAsync(user);
        return user;
    }

    public async Task UpdateUserAsync(User user)
    {
        await _userRepo.UpdateAsync(user);
    }

    public async Task<IEnumerable<RouteModel>> GetAllRoutesAsync()
    {
        var routes = await _routeRepo.GetAllAsync(includeProperties: "RoutePoints,GalleryPhotos");
        return routes.Select(MapRoute);
    }

    public async Task<RouteModel> GetRouteByKeyAsync(string key)
    {
        var route = await _routeRepo.GetFirstOrDefaultAsync(r => r.Slug == key, includeProperties: "RoutePoints,GalleryPhotos");
        return route != null ? MapRoute(route) : null;
    }

    public async Task<IEnumerable<GalleryPhotoModel>> GetAllPhotosAsync()
    {
        var photos = await _galleryRepo.GetAllAsync(includeProperties: "Route");
        return photos.Select(p => new GalleryPhotoModel
        {
            FileName = p.Url,
            RouteKey = p.Route?.Slug ?? "Всі"
        });
    }

    public async Task<IEnumerable<BookingModel>> GetUserBookingsAsync(int userId)
    {
        var bookings = await _bookingRepo.GetAllAsync(b => b.UserId == userId, includeProperties: "Route");
        return bookings.Select(b => new BookingModel
        {
            Id = b.Id,
            Name = b.GuestName,
            Phone = b.GuestPhone,
            Email = b.GuestEmail,
            RouteName = b.Route?.Name ?? "Невідомий маршрут",
            PeopleCount = b.NumberOfPeople,
            CreatedAt = b.CreatedAt,
            Status = b.Status,
            UserId = b.UserId
        });
    }

    public async Task<BookingModel> CreateBookingAsync(BookingModel model)
    {
        var route = await _routeRepo.GetFirstOrDefaultAsync(r => r.Slug == model.RouteKey || r.Name == model.RouteName);
        if (route == null)
            throw new Exception($"Маршрут не знайдено: {model.RouteName}");

        var booking = new Booking
        {
            GuestName = model.Name ?? string.Empty,
            GuestPhone = model.Phone ?? string.Empty,
            GuestEmail = model.Email ?? string.Empty,
            RouteId = route.Id,
            NumberOfPeople = model.PeopleCount,
            CreatedAt = DateTime.UtcNow,
            TourDate = model.TourDate == default ? DateTime.UtcNow.AddDays(7) : model.TourDate,
            Status = "Очікує",
            UserId = model.UserId
        };
        await _bookingRepo.AddAsync(booking);
        model.Id = booking.Id;
        model.Status = booking.Status;
        model.CreatedAt = booking.CreatedAt;
        return model;
    }

    private RouteModel MapRoute(Route r)
    {
        var model = new RouteModel
        {
            Key = r.Slug,
            Title = r.Name,
            MapImage = r.ImageUrl,
            InteractiveUrl = r.MapUrl,
            Difficulty = r.Difficulty,
            Length = r.DistanceKm + " км",
            Duration = r.DurationHours + " год"
        };
        if (r.RoutePoints != null)
        {
            foreach (var p in r.RoutePoints.OrderBy(x => x.OrderIndex))
                model.Points.Add(new RoutePointModel
                {
                    Title = p.Name,
                    Altitude = p.Latitude.ToString(),
                    Description = p.Description,
                    Image = string.Empty,
                    Order = p.OrderIndex
                });
        }
        if (r.GalleryPhotos != null)
        {
            foreach (var p in r.GalleryPhotos)
                model.Photos.Add(new GalleryPhotoModel { FileName = p.Url, RouteKey = r.Slug });
        }
        return model;
    }

    public async Task EnsureSeedDataAsync()
    {
        var routes = await _routeRepo.GetAllAsync();
        if (routes.Any()) return;

        var shpitsi = new Route { Slug = "SHPITSI", Name = "МАРШРУТ: ШПИЦІ", ImageUrl = "shpitsi.png", MapUrl = "https://mapy.cz/s/dekodejapa", Difficulty = "Середній", DistanceKm = 24, DurationHours = 12, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        shpitsi.RoutePoints.Add(new RoutePoint { Name = "Заросляк", Latitude = 1330, Description = "Тихий початок серед смерек і туману. Тут ліс дихає, а стежка тільки народжується.", OrderIndex = 1 });
        shpitsi.RoutePoints.Add(new RoutePoint { Name = "Озеро Несамовите", Latitude = 1750, Description = "Озеро легенд, де вітер шепоче давні історії. Вода як дзеркало неба.", OrderIndex = 2 });
        shpitsi.RoutePoints.Add(new RoutePoint { Name = "Гора Шпиці", Latitude = 1863, Description = "Скелясті піки, немов кам'яні вартові. Тут панує тиша, вітер і велич.", OrderIndex = 3 });
        shpitsi.RoutePoints.Add(new RoutePoint { Name = "Повернення", Latitude = 0, Description = "Спуск крізь світло і тіні. Повернення з гір схоже на пробудження від сну.", OrderIndex = 4 });
        shpitsi.GalleryPhotos.Add(new GalleryPhoto { Url = "shpitsi2.png" });
        shpitsi.GalleryPhotos.Add(new GalleryPhoto { Url = "nesamovyte.png" });
        shpitsi.GalleryPhotos.Add(new GalleryPhoto { Url = "zaroslyak.png" });
        shpitsi.GalleryPhotos.Add(new GalleryPhoto { Url = "rebra.png" });
        await _routeRepo.AddAsync(shpitsi);

        var pipivan = new Route { Slug = "PIPIVAN", Name = "МАРШРУТ: ПІП ІВАН", ImageUrl = "pip_ivan.png", MapUrl = "https://mapy.cz/s/bebojufede", Difficulty = "Важкий", DistanceKm = 32, DurationHours = 24, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        pipivan.RoutePoints.Add(new RoutePoint { Name = "Дземброня", Latitude = 896, Description = "Село на краю світу, де тиша готує до висоти.", OrderIndex = 1 });
        pipivan.RoutePoints.Add(new RoutePoint { Name = "Вухатий Камінь", Latitude = 1864, Description = "Кам'яний велетень, що стоїть на варті.", OrderIndex = 2 });
        pipivan.RoutePoints.Add(new RoutePoint { Name = "Гора Піп Іван", Latitude = 2022, Description = "Обсерваторія в хмарах, де час зупиняється.", OrderIndex = 3 });
        pipivan.RoutePoints.Add(new RoutePoint { Name = "Гора Смотрич", Latitude = 1898, Description = "Гора, що мовчки споглядає світ.", OrderIndex = 4 });
        pipivan.RoutePoints.Add(new RoutePoint { Name = "Повернення", Latitude = 0, Description = "Спуск, наче повернення з іншої реальності.", OrderIndex = 5 });
        pipivan.GalleryPhotos.Add(new GalleryPhoto { Url = "dzembronya.png" });
        pipivan.GalleryPhotos.Add(new GalleryPhoto { Url = "vukh_kamen.png" });
        pipivan.GalleryPhotos.Add(new GalleryPhoto { Url = "pip_ivann.png" });
        pipivan.GalleryPhotos.Add(new GalleryPhoto { Url = "smotrych.png" });
        await _routeRepo.AddAsync(pipivan);

        var hoverla = new Route { Slug = "HOVERLA", Name = "МАРШРУТ: ГОВЕРЛА", ImageUrl = "hoverla.png", MapUrl = "https://mapy.cz/s/cajedobeje", Difficulty = "Середній", DistanceKm = 14, DurationHours = 8, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        hoverla.RoutePoints.Add(new RoutePoint { Name = "Заросляк", Latitude = 1330, Description = "Початок шляху до найвищої точки України.", OrderIndex = 1 });
        hoverla.RoutePoints.Add(new RoutePoint { Name = "Мала Говерла", Latitude = 1761, Description = "Проміжна точка, де відкриваються перші панорами.", OrderIndex = 2 });
        hoverla.RoutePoints.Add(new RoutePoint { Name = "Говерла", Latitude = 2061, Description = "Дах України, вітри та безкраї краєвиди.", OrderIndex = 3 });
        hoverla.RoutePoints.Add(new RoutePoint { Name = "Говерлянський водоспад", Latitude = 1450, Description = "Потужний потік холодної води серед лісу.", OrderIndex = 4 });
        hoverla.GalleryPhotos.Add(new GalleryPhoto { Url = "hoverla1.png" });
        hoverla.GalleryPhotos.Add(new GalleryPhoto { Url = "mala_hoverla.png" });
        hoverla.GalleryPhotos.Add(new GalleryPhoto { Url = "hoverla_waterfall.png" });
        hoverla.GalleryPhotos.Add(new GalleryPhoto { Url = "return_hoverla.png" });
        await _routeRepo.AddAsync(hoverla);

        var svydovets = new Route { Slug = "SVYDOVETS", Name = "МАРШРУТ: СВИДОВЕЦЬ", ImageUrl = "svydovets.png", MapUrl = "https://mapy.cz/s/dejemulube", Difficulty = "Важкий", DistanceKm = 45, DurationHours = 32, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        svydovets.RoutePoints.Add(new RoutePoint { Name = "Драгобрат", Latitude = 1400, Description = "Високогірний курорт, ворота до Свидовця.", OrderIndex = 1 });
        svydovets.RoutePoints.Add(new RoutePoint { Name = "Озеро Ворожеська", Latitude = 1460, Description = "Кришталеве озеро, заховане у льодовиковому карі.", OrderIndex = 2 });
        svydovets.RoutePoints.Add(new RoutePoint { Name = "Гора Догяска", Latitude = 1761, Description = "Висока гора з видом на озеро Герашаська.", OrderIndex = 3 });
        svydovets.RoutePoints.Add(new RoutePoint { Name = "Озеро Герашаська", Latitude = 1577, Description = "Одне з найкрасивіших озер хребта.", OrderIndex = 4 });
        svydovets.GalleryPhotos.Add(new GalleryPhoto { Url = "dragobrat.png" });
        svydovets.GalleryPhotos.Add(new GalleryPhoto { Url = "vorozeska_lake.png" });
        svydovets.GalleryPhotos.Add(new GalleryPhoto { Url = "doghiaska.png" });
        svydovets.GalleryPhotos.Add(new GalleryPhoto { Url = "herashasyka.png" });
        await _routeRepo.AddAsync(svydovets);

        var gorgany = new Route { Slug = "GORGANY", Name = "МАРШРУТ: ҐОРҐАНИ", ImageUrl = "gorgany.png", MapUrl = "https://mapy.cz/s/defamebome", Difficulty = "Легкий", DistanceKm = 18, DurationHours = 9, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        gorgany.RoutePoints.Add(new RoutePoint { Name = "Яремче", Latitude = 585, Description = "Курортне містечко, старт маршруту.", OrderIndex = 1 });
        gorgany.RoutePoints.Add(new RoutePoint { Name = "Полонина Явірник", Latitude = 1200, Description = "Відкриті простори та вівчарські колиби.", OrderIndex = 2 });
        gorgany.RoutePoints.Add(new RoutePoint { Name = "Явірник-Горган", Latitude = 1467, Description = "Кам'яні розсипи та густі жерепи.", OrderIndex = 3 });
        gorgany.RoutePoints.Add(new RoutePoint { Name = "Круглий Явірник", Latitude = 1250, Description = "Тиха місцина серед лісів.", OrderIndex = 4 });
        gorgany.GalleryPhotos.Add(new GalleryPhoto { Url = "yaremche.png" });
        gorgany.GalleryPhotos.Add(new GalleryPhoto { Url = "polony_yavirnyk.png" });
        gorgany.GalleryPhotos.Add(new GalleryPhoto { Url = "yavirnyk_gorgan.png" });
        gorgany.GalleryPhotos.Add(new GalleryPhoto { Url = "round_yavirnyk.png" });
        await _routeRepo.AddAsync(gorgany);

        var kukul = new Route { Slug = "KUKUL", Name = "МАРШРУТ: КУКУЛ", ImageUrl = "kukul.png", MapUrl = "https://mapy.cz/s/dejezakopo", Difficulty = "Легкий", DistanceKm = 20, DurationHours = 8, RoutePoints = new List<RoutePoint>(), GalleryPhotos = new List<GalleryPhoto>() };
        kukul.RoutePoints.Add(new RoutePoint { Name = "Лазещина", Latitude = 706, Description = "Старовинне село, звідки починається багато маршрутів.", OrderIndex = 1 });
        kukul.RoutePoints.Add(new RoutePoint { Name = "Полонина Кукул", Latitude = 1350, Description = "Місце з неймовірним видом на Чорногору.", OrderIndex = 2 });
        kukul.RoutePoints.Add(new RoutePoint { Name = "Гора Кукул", Latitude = 1539, Description = "Спокійна вершина, ідеальна для зимових походів.", OrderIndex = 3 });
        kukul.RoutePoints.Add(new RoutePoint { Name = "Татарів", Latitude = 750, Description = "Фініш маршруту в гірському селі.", OrderIndex = 4 });
        kukul.GalleryPhotos.Add(new GalleryPhoto { Url = "lazeshchyna.png" });
        kukul.GalleryPhotos.Add(new GalleryPhoto { Url = "kukul1.png" });
        kukul.GalleryPhotos.Add(new GalleryPhoto { Url = "tatriv.png" });
        kukul.GalleryPhotos.Add(new GalleryPhoto { Url = "return_lazeshchyna.png" });
        await _routeRepo.AddAsync(kukul);
    }
}