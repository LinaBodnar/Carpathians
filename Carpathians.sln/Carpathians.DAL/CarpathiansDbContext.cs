using Microsoft.EntityFrameworkCore;
using Carpathians.DAL.Entities;

namespace Carpathians.DAL
{
    public class CarpathiansDbContext : DbContext
    {
        public CarpathiansDbContext(DbContextOptions<CarpathiansDbContext> options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
        public DbSet<Route> Routes { get; set; }
        public DbSet<RoutePoint> RoutePoints { get; set; }
        public DbSet<RoutePointPhoto> RoutePointPhotos { get; set; }
        public DbSet<GalleryPhoto> GalleryPhotos { get; set; }
        public DbSet<Booking> Bookings { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Точки видаляються разом із маршрутом
            modelBuilder.Entity<RoutePoint>()
                .HasOne(rp => rp.Route)
                .WithMany(r => r.RoutePoints)
                .HasForeignKey(rp => rp.RouteId)
                .OnDelete(DeleteBehavior.Cascade);

            // Фотографії точок видаляються разом із точкою
            modelBuilder.Entity<RoutePointPhoto>()
                .HasOne(rpp => rpp.RoutePoint)
                .WithMany(rp => rp.Photos)
                .HasForeignKey(rpp => rpp.RoutePointId)
                .OnDelete(DeleteBehavior.Cascade);

            // Якщо маршрут видалено, фото в галереї просто втрачає прив'язку (стає у "Всі фото")
            modelBuilder.Entity<GalleryPhoto>()
                .HasOne(gp => gp.Route)
                .WithMany(r => r.GalleryPhotos)
                .HasForeignKey(gp => gp.RouteId)
                .OnDelete(DeleteBehavior.SetNull);

            // Заявки не видаляються каскадно, щоб зберегти фінансову звітність
            modelBuilder.Entity<Booking>()
                .HasOne(b => b.Route)
                .WithMany(r => r.Bookings)
                .HasForeignKey(b => b.RouteId)
                .OnDelete(DeleteBehavior.Restrict);

            // Якщо користувач видалив профіль, його заявка залишається як гостьова
            modelBuilder.Entity<Booking>()
                .HasOne(b => b.User)
                .WithMany(u => u.Bookings)
                .HasForeignKey(b => b.UserId)
                .OnDelete(DeleteBehavior.SetNull);
        }
    }
}