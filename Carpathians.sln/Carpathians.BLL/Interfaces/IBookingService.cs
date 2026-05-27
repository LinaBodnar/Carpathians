using System.Collections.Generic;
using System.Threading.Tasks;
using Carpathians.BLL.DTOs;

namespace Carpathians.BLL.Interfaces
{
    public interface IBookingService
    {
        Task<BookingDto> CreateBookingAsync(BookingDto bookingDto);
        Task<IEnumerable<BookingDto>> GetBookingsByUserIdAsync(int userId);
        Task<IEnumerable<BookingDto>> GetBookingsByGuestEmailAsync(string email);
        Task<bool> UpdateBookingStatusAsync(int bookingId, string status);
    }
}