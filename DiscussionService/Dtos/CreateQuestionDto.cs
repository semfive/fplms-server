using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public record CreateQuestionDto
    {
        [Required]
        [StringLength(1000)]
        public string? Content { get; set; }

        [Required]
        public string? SubjectId { get; set; }

        [Required]
        public string? StudentId { get; set; }
    }
}