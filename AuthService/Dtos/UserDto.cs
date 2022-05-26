using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record UserDto
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }

        [Required]
        public string Role { get; set; }
    }
}