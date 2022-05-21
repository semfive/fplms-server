using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record UserDto
    {
        [Required]
        public string Email { get; set; }

        [Required]
        public string Role { get; set; }
    }
}