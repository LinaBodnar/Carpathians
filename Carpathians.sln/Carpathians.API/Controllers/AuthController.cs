using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Carpathians.BLL.Interfaces;

namespace Carpathians.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IUserService _userService;

        public AuthController(IUserService userService)
        {
            _userService = userService;
        }

        // POST: api/auth/register
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterRequest request)
        {
            var user = await _userService.RegisterAsync(request.Name, request.Email, request.Password);
            if (user == null) return BadRequest(new { message = "Користувач з таким Email вже існує" });

            return Ok(user);
        }

        // POST: api/auth/login
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var user = await _userService.LoginAsync(request.Email, request.Password);
            if (user == null) return Unauthorized(new { message = "Невірний Email або пароль" });

            return Ok(user);
        }

        // GET: api/auth/profile/5
        [HttpGet("profile/{userId:int}")]
        public async Task<IActionResult> GetProfile(int userId)
        {
            var user = await _userService.GetProfileAsync(userId);
            if (user == null) return NotFound(new { message = "Профіль не знайдено" });

            return Ok(user);
        }
    }

    public class RegisterRequest
    {
        public string Name { get; set; } = string.Empty;
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }

    public class LoginRequest
    {
        public string Email { get; set; } = string.Empty;
        public string Password { get; set; } = string.Empty;
    }
}