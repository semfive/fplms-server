using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace DiscussionService.Models
{
    public class Answer
    {
        public Guid Id { get; set; }

        [Required(ErrorMessage = "Content is required")]
        [StringLength(1000, ErrorMessage = "Content cannot be longer than 1000 characters")]
        public string? Content { get; set; }

        [Required(ErrorMessage = "CreatedDate is required")]
        public DateTime CreatedDate { get; set; }

        public DateTime? ModifiedDate { get; set; }

        [Required]
        public bool Accepted { get; set; }

        [Required]
        public bool Removed { get; set; }

        public string? RemovedBy { get; set; }


        [ForeignKey(nameof(Student))]
        public Guid StudentId { get; set; }
        public Student? Student { get; set; }

        [ForeignKey(nameof(Question))]
        public Guid QuestionId { get; set; }
        public Question? Question { get; set; }
    }
}