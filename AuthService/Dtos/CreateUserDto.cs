using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record CreateUserDto
    {
        public string Code { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public string Picture { get; set; }
        public bool IsLecturer { get; set; }
        public string ImageUrl { get; set; }
    }
}