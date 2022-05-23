using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class Student
    {
        public Guid Id { get; set; }

        [Required]
        [StringLength(50)]
        public string? Name { get; set; }

        [Required]
        [StringLength(50)]
        public string? Email { get; set; }

        [Required]
        [StringLength(1000)]
        public string? Picture { get; set; }

        public ICollection<Question>? Questions { get; set; }
        public ICollection<Answer>? Answers { get; set; }
    }
}