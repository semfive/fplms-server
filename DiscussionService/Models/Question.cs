using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class Question
    {
        public Guid Id { get; set; }

        [Required]
        [StringLength(250)]
        public string? Title { get; set; }

        [Required]
        [StringLength(20000)]
        public string? Content { get; set; }

        [Required]
        public DateTimeOffset CreatedDate { get; set; } = DateTimeOffset.UtcNow;
        public DateTimeOffset? ModifiedDate { get; set; } = DateTimeOffset.UtcNow;

        public bool Removed { get; set; } = false;
        public string? RemovedBy { get; set; }

        [ForeignKey(nameof(Subject))]
        public Guid SubjectId { get; set; }
        public Subject? Subject { get; set; }

        [ForeignKey(nameof(Student))]
        public Guid StudentId { get; set; }
        public Student? Student { get; set; }

        public ICollection<Answer>? Answers { get; set; }
    }
}