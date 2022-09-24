using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public class CreateLecturerDto
    {
        [Required]
        [StringLength(50)]
        public string? Name { get; set; }

        [Required]
        [EmailAddress]
        public string? Email { get; set; }

        [Required]
        [Url]
        public string? Picture { get; set; }
    }
}