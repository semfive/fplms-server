using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public record CreateAnswerDto
    {
        [Required]
        [StringLength(1000)]
        public string? Content { get; set; }

        [Required]
        public Guid QuestionId { get; set; }

        // [Required]
        // public Guid StudentId { get; set; }
    }
}