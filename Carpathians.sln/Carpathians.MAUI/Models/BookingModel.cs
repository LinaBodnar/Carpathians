using System;

namespace Carpathians.MAUI.Models;

public class BookingModel
{
    public int Id { get; set; }
    public string? Name { get; set; }
    public string? Phone { get; set; }
    public string? Email { get; set; }
    public string? RouteName { get; set; }
    public string? RouteKey { get; set; }
    public int PeopleCount { get; set; } = 1;
    public DateTime TourDate { get; set; }
    public DateTime CreatedAt { get; set; }
    public string? Status { get; set; }
    public int? UserId { get; set; }
}