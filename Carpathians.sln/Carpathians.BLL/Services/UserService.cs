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
    public class UserService : IUserService
    {
        private readonly IGenericRepository<User> _userRepository;
        private readonly IMapper _mapper;

        public UserService(IGenericRepository<User> userRepository, IMapper mapper)
        {
            _userRepository = userRepository;
            _mapper = mapper;
        }

        public async Task<UserDto?> RegisterAsync(string name, string email, string password)
        {
            var existingUser = await _userRepository.GetFirstOrDefaultAsync(u => u.Email.ToLower() == email.ToLower());
            if (existingUser != null) return null;

            var user = new User
            {
                Name = name,
                Email = email,
                PasswordHash = BCrypt.Net.BCrypt.HashPassword(password),
                AvatarUrl = null
            };

            await _userRepository.AddAsync(user);
            return _mapper.Map<UserDto>(user);
        }

        public async Task<UserDto?> LoginAsync(string email, string password)
        {
            var user = await _userRepository.GetFirstOrDefaultAsync(u => u.Email.ToLower() == email.ToLower());
            if (user == null) return null;

            bool isPasswordValid = BCrypt.Net.BCrypt.Verify(password, user.PasswordHash);
            if (!isPasswordValid) return null;

            return _mapper.Map<UserDto>(user);
        }

        public async Task<UserDto?> GetProfileAsync(int userId)
        {
            var user = await _userRepository.GetByIdAsync(userId);
            return _mapper.Map<UserDto>(user);
        }
    }
}