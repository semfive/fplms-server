using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Models
{
    public class Lecturer
    {
        public Guid Id { get; set; }

        [Required]
        [StringLength(50)]
        public string Name { get; set; } = null!;

        [Required]
        [StringLength(50)]
        public string Email { get; set; } = null!;

        [Required]
        [StringLength(1000)]
        public string Picture { get; set; } = null!;
    }
}