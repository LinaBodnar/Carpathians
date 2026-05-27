using System.Threading.Tasks;
using Carpathians.BLL.DTOs;

namespace Carpathians.BLL.Interfaces
{
    public interface IUserService
    {
        Task<UserDto?> RegisterAsync(string name, string email, string password);
        Task<UserDto?> LoginAsync(string email, string password);
        Task<UserDto?> GetProfileAsync(int userId);
    }
}