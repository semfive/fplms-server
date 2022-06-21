using DiscussionService.Models;

namespace DiscussionService.Dtos
{
    public class GetAnswerDto
    {
        public Guid Id { get; set; }
        public string? Content { get; set; }
        public bool Accepted { get; set; }
        public DateTimeOffset CreatedDate { get; set; }
        public DateTimeOffset ModifiedDate { get; set; }
        public bool Removed { get; set; }
        public string? RemovedBy { get; set; }
        public GetStudentDto Student { get; set; }
        public Guid QuestionId { get; set; }
    }
}