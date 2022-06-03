using System.ComponentModel.DataAnnotations;

namespace AuthService.Dtos
{
    public record ManagementCreateUserDto
    {
        public string code { get; set; }
        public string name { get; set; }
        public string email { get; set; }
        public bool isLecturer { get; set; }
        public string imageUrl { get; set; }
    }
}