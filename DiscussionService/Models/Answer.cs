using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class Answer
    {
        public Guid Id { get; set; }

        [Required]
        [StringLength(1000)]
        public string? Content { get; set; }

        [Required]
        public DateTimeOffset CreatedDate { get; set; } = DateTimeOffset.UtcNow;

        public DateTimeOffset? ModifiedDate { get; set; } = DateTimeOffset.UtcNow;

        [Required]
        public bool Accepted { get; set; }

        [Required]
        public bool Removed { get; set; } = false;

        public string? RemovedBy { get; set; }

        [ForeignKey(nameof(Student))]
        public Guid StudentId { get; set; }
        public Student Student { get; set; }

        [ForeignKey(nameof(Question))]
        public Guid QuestionId { get; set; }
        public Question Question { get; set; }

        public ICollection<StudentAnswerUpvote> Upvoters { get; set; }
    }
}