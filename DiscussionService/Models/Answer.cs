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
        public DateTime CreatedDate { get; set; }

        public DateTime? ModifiedDate { get; set; }

        [Required]
        public bool Accepted { get; set; }

        [Required]
        public bool Removed { get; set; }

        public string? RemovedBy { get; set; }

        [ForeignKey(nameof(Student))]
        public Guid StudentId { get; set; }
        public Student Student { get; set; }

        [ForeignKey(nameof(Question))]
        public Guid QuestionId { get; set; }
        public Question Question { get; set; }
    }
}