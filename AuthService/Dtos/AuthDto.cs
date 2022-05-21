using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record AuthDto
    {
        [Required]
        public string Provider { get; set; }

        [Required]
        public string IdToken { get; set; }
    }
}