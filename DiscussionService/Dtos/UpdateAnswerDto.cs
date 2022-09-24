using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public record UpdateAnswerDto
    {
        [Required]
        [StringLength(1000)]
        public string? Content { get; set; }
    }
}