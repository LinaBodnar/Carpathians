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

            modelBuilder.Entity<RoutePoint>()
                .HasOne(rp => rp.Route)
                .WithMany(r => r.RoutePoints)
                .HasForeignKey(rp => rp.RouteId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<RoutePointPhoto>()
                .HasOne(rpp => rpp.RoutePoint)
                .WithMany(rp => rp.Photos)
                .HasForeignKey(rpp => rpp.RoutePointId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<GalleryPhoto>()
                .HasOne(gp => gp.Route)
                .WithMany(r => r.GalleryPhotos)
                .HasForeignKey(gp => gp.RouteId)
                .OnDelete(DeleteBehavior.SetNull);

            modelBuilder.Entity<Booking>()
                .HasOne(b => b.Route)
                .WithMany(r => r.Bookings)
                .HasForeignKey(b => b.RouteId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Booking>()
                .HasOne(b => b.User)
                .WithMany(u => u.Bookings)
                .HasForeignKey(b => b.UserId)
                .OnDelete(DeleteBehavior.SetNull);
        }
    }
}