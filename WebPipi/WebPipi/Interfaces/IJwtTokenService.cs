using WebPipi.Data.Entities.Identity;

namespace WebPipi.Interfaces
{
    public interface IJwtTokenService
    {
        Task<string> CreateToken(UserEntity user);
    }
}
