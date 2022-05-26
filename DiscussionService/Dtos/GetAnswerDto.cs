using DiscussionService.Models;

namespace DiscussionService.Dtos
{
    public class GetAnswerDto
    {
        public Guid Id { get; set; }
        public string? Content { get; set; }
        public DateTime CreatedDate { get; set; }
        public DateTime ModifiedDate { get; set; }
        public bool Removed { get; set; }
        public string? RemovedBy { get; set; }
        public Guid StudentId { get; set; }
        public Guid QuestionId { get; set; }
    }
}