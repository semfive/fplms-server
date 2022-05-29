using System.ComponentModel.DataAnnotations;

namespace DiscussionService.Dtos
{
    public class CreateSubjectDto
    {
        [Required]
        [StringLength(7)]
        public string? Name { get; set; }
    }
}