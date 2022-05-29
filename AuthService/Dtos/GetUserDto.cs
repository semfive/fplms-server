using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record GetUserDto
    {
        public string Email { get; set; }
        public string Role { get; set; }
    }
}