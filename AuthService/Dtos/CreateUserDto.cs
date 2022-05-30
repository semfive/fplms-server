using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record CreateUserDto
    {
        public string Name { get; set; }
        public string Email { get; set; }
        public string Picture { get; set; }
    }
}