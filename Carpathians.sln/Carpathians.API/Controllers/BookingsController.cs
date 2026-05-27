using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Carpathians.BLL.DTOs;
using Carpathians.BLL.Interfaces;

namespace Carpathians.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class BookingsController : ControllerBase
    {
        private readonly IBookingService _bookingService;

        public BookingsController(IBookingService bookingService)
        {
            _bookingService = bookingService;
        }

        // POST: api/bookings (Створення нової заявки)
        [HttpPost]
        public async Task<IActionResult> CreateBooking([FromBody] BookingDto bookingDto)
        {
            if (!ModelState.IsValid) return BadRequest(ModelState);

            var result = await _bookingService.CreateBookingAsync(bookingDto);
            return CreatedAtAction(nameof(CreateBooking), new { id = result.Id }, result);
        }

        // GET: api/bookings/user/5 (Заявки конкретного юзера)
        [HttpGet("user/{userId:int}")]
        public async Task<IActionResult> GetUserBookings(int userId)
        {
            var bookings = await _bookingService.GetBookingsByUserIdAsync(userId);
            return Ok(bookings);
        }

        // GET: api/bookings/guest?email=test@mail.com (Заявки для гостей)
        [HttpGet("guest")]
        public async Task<IActionResult> GetGuestBookings([FromQuery] string email)
        {
            if (string.IsNullOrEmpty(email)) return BadRequest(new { message = "Email обов'язковий" });

            var bookings = await _bookingService.GetBookingsByGuestEmailAsync(email);
            return Ok(bookings);
        }
    }
}