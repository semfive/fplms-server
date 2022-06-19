using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public record UpdateQuestionDto
    {
        [StringLength(250)]
        [Required]
        public string? Title { get; set; }

        [StringLength(1000)]
        [Required]
        public string? Content { get; set; }

        [StringLength(10)]
        [Required]
        public string? SubjectName { get; set; }
    }
}