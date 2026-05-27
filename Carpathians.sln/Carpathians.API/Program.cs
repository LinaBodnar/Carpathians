using Carpathians.BLL.Interfaces;
using Carpathians.BLL.Services;
using Microsoft.EntityFrameworkCore;
using Carpathians.DAL;

var builder = WebApplication.CreateBuilder(args);

// Додаємо підтримку контролерів
builder.Services.AddControllers();

// Підключаємо наш CarpathiansDbContext до PostgreSQL
builder.Services.AddDbContext<CarpathiansDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Реєстрація Generic Repository
builder.Services.AddScoped(typeof(Carpathians.DAL.Interfaces.IGenericRepository<>), typeof(Carpathians.DAL.Repositories.GenericRepository<>));

// Правильний синтаксис передачі збірки для нових версій AutoMapper
builder.Services.AddAutoMapper(cfg => { }, System.Reflection.Assembly.Load("Carpathians.BLL"));

// Реєстрація бізнес-сервісів за патерном Dependency Injection
builder.Services.AddScoped<IRouteService, RouteService>();

// Налаштування Swagger для тестування API
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Налаштування HTTP-конвеєра
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

app.Run();