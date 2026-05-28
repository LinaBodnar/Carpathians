using Carpathians.BLL.Interfaces;
using Carpathians.BLL.Services;
using Microsoft.EntityFrameworkCore;
using Carpathians.DAL;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

builder.Services.AddDbContext<CarpathiansDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddScoped(typeof(Carpathians.DAL.Interfaces.IGenericRepository<>), typeof(Carpathians.DAL.Repositories.GenericRepository<>));

builder.Services.AddAutoMapper(cfg => { }, System.Reflection.Assembly.Load("Carpathians.BLL"));

builder.Services.AddScoped<IRouteService, RouteService>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddAutoMapper(cfg => { }, System.Reflection.Assembly.Load("Carpathians.BLL"));

builder.Services.AddScoped(typeof(Carpathians.DAL.Interfaces.IGenericRepository<>), typeof(Carpathians.DAL.Repositories.GenericRepository<>));

builder.Services.AddScoped<Carpathians.BLL.Interfaces.IRouteService, Carpathians.BLL.Services.RouteService>();
builder.Services.AddScoped<Carpathians.BLL.Interfaces.IBookingService, Carpathians.BLL.Services.BookingService>();
builder.Services.AddScoped<Carpathians.BLL.Interfaces.IGalleryService, Carpathians.BLL.Services.GalleryService>();
builder.Services.AddScoped<Carpathians.BLL.Interfaces.IUserService, Carpathians.BLL.Services.UserService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();