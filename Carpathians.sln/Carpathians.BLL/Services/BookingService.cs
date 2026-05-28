using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using AutoMapper;
using Carpathians.BLL.DTOs;
using Carpathians.BLL.Interfaces;
using Carpathians.DAL.Entities;
using Carpathians.DAL.Interfaces;

namespace Carpathians.BLL.Services
{
    public class BookingService : IBookingService
    {
        private readonly IGenericRepository<Booking> _bookingRepository;
        private readonly IMapper _mapper;

        public BookingService(IGenericRepository<Booking> bookingRepository, IMapper mapper)
        {
            _bookingRepository = bookingRepository;
            _mapper = mapper;
        }

        public async Task<BookingDto> CreateBookingAsync(BookingDto bookingDto)
        {
            var booking = _mapper.Map<Booking>(bookingDto);
            booking.Status = "pending";
            booking.CreatedAt = DateTime.UtcNow;

            await _bookingRepository.AddAsync(booking);
            return _mapper.Map<BookingDto>(booking);
        }

        public async Task<IEnumerable<BookingDto>> GetBookingsByUserIdAsync(int userId)
        {
            var bookings = await _bookingRepository.GetAllAsync(b => b.UserId == userId, includeProperties: "Route");
            return _mapper.Map<IEnumerable<BookingDto>>(bookings);
        }

        public async Task<IEnumerable<BookingDto>> GetBookingsByGuestEmailAsync(string email)
        {
            var bookings = await _bookingRepository.GetAllAsync(b => b.GuestEmail.ToLower() == email.ToLower(), includeProperties: "Route");
            return _mapper.Map<IEnumerable<BookingDto>>(bookings);
        }

        public async Task<bool> UpdateBookingStatusAsync(int bookingId, string status)
        {
            var booking = await _bookingRepository.GetByIdAsync(bookingId);
            if (booking == null) return false;

            booking.Status = status;
            await _bookingRepository.UpdateAsync(booking);
            return true;
        }
    }
}