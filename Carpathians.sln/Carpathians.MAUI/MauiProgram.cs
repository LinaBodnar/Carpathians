using Microsoft.Extensions.Logging;
using Carpathians.DAL;
using Carpathians.DAL.Interfaces;
using Carpathians.DAL.Repositories;
using Carpathians.MAUI.Services;
using Carpathians.MAUI.ViewModels;
using Carpathians.MAUI.Views;
using Microsoft.EntityFrameworkCore;
using System.IO;
using Microsoft.Maui.Storage;

namespace Carpathians.MAUI;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder
            .UseMauiApp<App>()
            .ConfigureFonts(fonts =>
            {
                fonts.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                fonts.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
                fonts.AddFont("DMSerifText-Regular.ttf", "DMSerif");
            });

#if DEBUG
        builder.Logging.AddDebug();
#endif

        var dbPath = Path.Combine(FileSystem.AppDataDirectory, "carpathians.db");
        builder.Services.AddDbContext<CarpathiansDbContext>(opt =>
            opt.UseSqlite($"Data Source={dbPath}"),
            ServiceLifetime.Transient);

        builder.Services.AddTransient(typeof(IGenericRepository<>), typeof(GenericRepository<>));
        builder.Services.AddSingleton<SessionService>();
        builder.Services.AddTransient<IDataService, LocalDataService>();

        builder.Services.AddTransient<RoutesViewModel>();
        builder.Services.AddTransient<RouteDetailViewModel>();
        builder.Services.AddTransient<BookingViewModel>();
        builder.Services.AddTransient<GalleryViewModel>();
        builder.Services.AddTransient<ProfileViewModel>();

        builder.Services.AddTransient<RoutesPage>();
        builder.Services.AddTransient<RouteDetailPage>();
        builder.Services.AddTransient<MapViewPage>();
        builder.Services.AddTransient<BookingPage>();
        builder.Services.AddTransient<GalleryPage>();
        builder.Services.AddTransient<ProfilePage>();

        var app = builder.Build();

        using (var scope = app.Services.CreateScope())
        {
            var db = scope.ServiceProvider.GetRequiredService<CarpathiansDbContext>();
            db.Database.EnsureCreated();
            var dataService = scope.ServiceProvider.GetRequiredService<IDataService>();
            dataService.EnsureSeedDataAsync().Wait();
        }

        return app;
    }
}